/*
    Copyright 2005-2012 Intel Corporation.  All Rights Reserved.

    This file is part of Threading Building Blocks.

    Threading Building Blocks is free software; you can redistribute it
    and/or modify it under the terms of the GNU General Public License
    version 2 as published by the Free Software Foundation.

    Threading Building Blocks is distributed in the hope that it will be
    useful, but WITHOUT ANY WARRANTY; without even the implied warranty
    of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Threading Building Blocks; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    As a special exception, you may use this file as part of a free software
    library without restriction.  Specifically, if other files instantiate
    templates or use macros or inline functions from this file, or you compile
    this file and link it with other files to produce an executable, this
    file does not by itself cause the resulting executable to be covered by
    the GNU General Public License.  This exception does not however
    invalidate any other reasons why the executable file might be covered by
    the GNU General Public License.
*/

#include "scheduler.h"

#include "tbb/task.h"
#include "tbb/tbb_exception.h"
#include "tbb/cache_aligned_allocator.h"
#include "itt_notify.h"

namespace tbb {

#if __TBB_TASK_GROUP_CONTEXT

using namespace internal;

//------------------------------------------------------------------------
// captured_exception
//------------------------------------------------------------------------

inline char* duplicate_string ( const char* src ) {
    char* dst = NULL;
    if ( src ) {
        size_t len = strlen(src) + 1;
        dst = (char*)allocate_via_handler_v3(len);
        strncpy (dst, src, len);
    }
    return dst;
}

captured_exception::~captured_exception () throw() {
    clear();
}

void captured_exception::set ( const char* name, const char* info ) throw() {
    my_exception_name = duplicate_string( name );
    my_exception_info = duplicate_string( info );
}

void captured_exception::clear () throw() {
    deallocate_via_handler_v3 (const_cast<char*>(my_exception_name));
    deallocate_via_handler_v3 (const_cast<char*>(my_exception_info));
}

captured_exception* captured_exception::move () throw() {
    captured_exception *e = (captured_exception*)allocate_via_handler_v3(sizeof(captured_exception));
    if ( e ) {
        ::new (e) captured_exception();
        e->my_exception_name = my_exception_name;
        e->my_exception_info = my_exception_info;
        e->my_dynamic = true;
        my_exception_name = my_exception_info = NULL;
    }
    return e;
}

void captured_exception::destroy () throw() {
    __TBB_ASSERT ( my_dynamic, "Method destroy can be used only on objects created by clone or allocate" );
    if ( my_dynamic ) {
        this->captured_exception::~captured_exception();
        deallocate_via_handler_v3 (this);
    }
}

captured_exception* captured_exception::allocate ( const char* name, const char* info ) {
    captured_exception *e = (captured_exception*)allocate_via_handler_v3( sizeof(captured_exception) );
    if ( e ) {
        ::new (e) captured_exception(name, info);
        e->my_dynamic = true;
    }
    return e;
}

const char* captured_exception::name() const throw() {
    return my_exception_name;
}

const char* captured_exception::what() const throw() {
    return my_exception_info;
}


//------------------------------------------------------------------------
// tbb_exception_ptr
//------------------------------------------------------------------------

#if !TBB_USE_CAPTURED_EXCEPTION

namespace internal {

template<typename T>
tbb_exception_ptr* AllocateExceptionContainer( const T& src ) {
    tbb_exception_ptr *eptr = (tbb_exception_ptr*)allocate_via_handler_v3( sizeof(tbb_exception_ptr) );
    if ( eptr )
        new (eptr) tbb_exception_ptr(src);
    return eptr;
}

tbb_exception_ptr* tbb_exception_ptr::allocate () {
    return AllocateExceptionContainer( std::current_exception() );
}

tbb_exception_ptr* tbb_exception_ptr::allocate ( const tbb_exception& ) {
    return AllocateExceptionContainer( std::current_exception() );
}

tbb_exception_ptr* tbb_exception_ptr::allocate ( captured_exception& src ) {
    tbb_exception_ptr *res = AllocateExceptionContainer( src );
    src.destroy();
    return res;
}

void tbb_exception_ptr::destroy () throw() {
    this->tbb_exception_ptr::~tbb_exception_ptr();
    deallocate_via_handler_v3 (this);
}

} // namespace internal
#endif /* !TBB_USE_CAPTURED_EXCEPTION */


//------------------------------------------------------------------------
// task_group_context
//------------------------------------------------------------------------

task_group_context::~task_group_context () {
    if ( my_kind == binding_completed ) {
        if ( governor::is_set(my_owner) ) {
            // Local update of the context list 
            uintptr_t local_count_snapshot = my_owner->my_context_state_propagation_epoch;
            my_owner->my_local_ctx_list_update = 1;
            // Prevent load of nonlocal update flag from being hoisted before the
            // store to local update flag.
            atomic_fence();
            if ( my_owner->my_nonlocal_ctx_list_update ) {
                spin_mutex::scoped_lock lock(my_owner->my_context_list_mutex);
                my_node.my_prev->my_next = my_node.my_next;
                my_node.my_next->my_prev = my_node.my_prev;
                my_owner->my_local_ctx_list_update = 0;
            }
            else {
                my_node.my_prev->my_next = my_node.my_next;
                my_node.my_next->my_prev = my_node.my_prev;
                // Release fence is necessary so that update of our neighbors in 
                // the context list was committed when possible concurrent destroyer
                // proceeds after local update flag is reset by the following store.
                __TBB_store_with_release( my_owner->my_local_ctx_list_update, 0 );
                if ( local_count_snapshot != the_context_state_propagation_epoch ) {
                    // Another thread was propagating cancellation request when we removed
                    // ourselves from the list. We must ensure that it is not accessing us 
                    // when this destructor finishes. We'll be able to acquire the lock 
                    // below only after the other thread finishes with us.
                    spin_mutex::scoped_lock lock(my_owner->my_context_list_mutex);
                }
            }
        }
        else {
            // Nonlocal update of the context list 
            if ( __TBB_FetchAndStoreW(&my_kind, dying) == detached ) {
                my_node.my_prev->my_next = my_node.my_next;
                my_node.my_next->my_prev = my_node.my_prev;
            }
            else {
                __TBB_FetchAndAddW(&my_owner->my_nonlocal_ctx_list_update, 1);
                spin_wait_until_eq( my_owner->my_local_ctx_list_update, 0u );
                my_owner->my_context_list_mutex.lock();
                my_node.my_prev->my_next = my_node.my_next;
                my_node.my_next->my_prev = my_node.my_prev;
                my_owner->my_context_list_mutex.unlock();
                __TBB_FetchAndAddW(&my_owner->my_nonlocal_ctx_list_update, -1);
            }
        }
    }
    poison_value(my_version_and_traits);
    if ( my_exception )
        my_exception->destroy();
    ITT_STACK(itt_caller != ITT_CALLER_NULL, caller_destroy, itt_caller);
}

void task_group_context::init () {
    __TBB_ASSERT ( sizeof(uintptr_t) < 32, "Layout of my_version_and_traits must be reconsidered on this platform" );
    __TBB_ASSERT ( sizeof(task_group_context) == 2 * NFS_MaxLineSize, "Context class has wrong size - check padding and members alignment" );
    __TBB_ASSERT ( (uintptr_t(this) & (sizeof(my_cancellation_requested) - 1)) == 0, "Context is improperly aligned" );
    __TBB_ASSERT ( my_kind == isolated || my_kind == bound, "Context can be created only as isolated or bound" );
    my_parent = NULL;
    my_cancellation_requested = 0;
    my_exception = NULL;
    my_owner = NULL;
    my_state = 0;
    itt_caller = ITT_CALLER_NULL;
#if __TBB_TASK_PRIORITY
    my_priority = normalized_normal_priority;
#endif /* __TBB_TASK_PRIORITY */
}

void task_group_context::register_with ( generic_scheduler *local_sched ) {
    __TBB_ASSERT( local_sched, NULL );
    my_owner = local_sched;
    my_node.my_prev = &local_sched->my_context_list_head;
    // Notify threads that may be concurrently destroying contexts registered
    // in this scheduler's list that local list update is underway.
    local_sched->my_local_ctx_list_update = 1;
    // Prevent load of global propagation epoch counter from being hoisted before 
    // speculative stores above, as well as load of nonlocal update flag from
    // being hoisted before the store to local update flag.
    atomic_fence();
    // Finalize local context list update
    if ( local_sched->my_nonlocal_ctx_list_update ) {
        spin_mutex::scoped_lock lock(my_owner->my_context_list_mutex);
        local_sched->my_context_list_head.my_next->my_prev = &my_node;
        my_node.my_next = local_sched->my_context_list_head.my_next;
        my_owner->my_local_ctx_list_update = 0;
        local_sched->my_context_list_head.my_next = &my_node;
    }
    else {
        local_sched->my_context_list_head.my_next->my_prev = &my_node;
        my_node.my_next = local_sched->my_context_list_head.my_next;
        __TBB_store_with_release( my_owner->my_local_ctx_list_update, 0 );
        // Thread local list of contexts allows concurrent traversal by another thread 
        // while propagating state change. To ensure visibility of my_node's members
        // to the concurrently traversing thread, the list's head is updated by means
        // of store-with-release.
        __TBB_store_with_release(local_sched->my_context_list_head.my_next, &my_node);
    }
}

void task_group_context::bind_to ( generic_scheduler *local_sched ) {
    __TBB_ASSERT ( my_kind == binding_required, "Already bound or isolated?" );
    __TBB_ASSERT ( !my_parent, "Parent is set before initial binding" );
    my_parent = local_sched->my_innermost_running_task->prefix().context;

    // Condition below prevents unnecessary thrashing parent context's cache line
    if ( !(my_parent->my_state & may_have_children) )
        my_parent->my_state |= may_have_children;
    if ( my_parent->my_parent ) {
        // Even if this context were made accessible for state change propagation
        // (by placing __TBB_store_with_release(s->my_context_list_head.my_next, &my_node)
        // above), it still could be missed if state propagation from a grand-ancestor
        // was underway concurrently with binding.
        // Speculative propagation from the parent together with epoch counters 
        // detecting possibility of such a race allow to avoid taking locks when
        // there is no contention.

        // Acquire fence is necessary to prevent reordering subsequent speculative
        // loads of parent state data out of the scope where epoch counters comparison
        // can reliably validate it.
        uintptr_t local_count_snapshot = __TBB_load_with_acquire( my_parent->my_owner->my_context_state_propagation_epoch );
        // Speculative propagation of parent's state. The speculation will be 
        // validated by the epoch counters check further on.
        my_cancellation_requested = my_parent->my_cancellation_requested;
#if __TBB_TASK_PRIORITY
        my_priority = my_parent->my_priority;
#endif /* __TBB_TASK_PRIORITY */
        register_with( local_sched ); // Issues full fence

        // If no state propagation was detected by the following condition, the above 
        // full fence guarantees that the parent had correct state during speculative
        // propagation before the fence. Otherwise the propagation from parent is
        // repeated under the lock.
        if ( local_count_snapshot != the_context_state_propagation_epoch ) {
            // Another thread may be propagating state change right now. So resort to lock.
            spin_mutex::scoped_lock lock(the_context_state_propagation_mutex);
            my_cancellation_requested = my_parent->my_cancellation_requested;
#if __TBB_TASK_PRIORITY
            my_priority = my_parent->my_priority;
#endif /* __TBB_TASK_PRIORITY */
        }
    }
    else {
        register_with( local_sched ); // Issues full fence
        // As we do not have grand-ancestors, concurrent state propagation (if any)
        // may originate only from the parent context, and thus it is safe to directly
        // copy the state from it.
        my_cancellation_requested = my_parent->my_cancellation_requested;
#if __TBB_TASK_PRIORITY
        my_priority = my_parent->my_priority;
#endif /* __TBB_TASK_PRIORITY */
    }
    my_kind = binding_completed;
}

#if __TBB_TASK_GROUP_CONTEXT
template <typename T>
void task_group_context::propagate_state_from_ancestors ( T task_group_context::*mptr_state, T new_state ) {
    task_group_context *ancestor = my_parent;
    while ( ancestor && ancestor->*mptr_state != new_state )
        ancestor = ancestor->my_parent;
    if ( ancestor ) {
        task_group_context *ctx = this;
        do {
            ctx->*mptr_state = new_state;
            ctx = ctx->my_parent;
        } while ( ctx != ancestor );
    }
}

template <typename T>
void generic_scheduler::propagate_task_group_state ( T task_group_context::*mptr_state, T new_state ) {
    spin_mutex::scoped_lock lock(my_context_list_mutex);
    // Acquire fence is necessary to ensure that the subsequent node->my_next load 
    // returned the correct value in case it was just inserted in another thread.
    // The fence also ensures visibility of the correct my_parent value.
    context_list_node_t *node = __TBB_load_with_acquire(my_context_list_head.my_next);
    while ( node != &my_context_list_head ) {
        task_group_context &ctx = __TBB_get_object_ref(task_group_context, my_node, node);
        if ( ctx.*mptr_state != new_state )
            ctx.propagate_state_from_ancestors( mptr_state, new_state );
        node = node->my_next;
        __TBB_ASSERT( is_alive(ctx.my_version_and_traits), "Local context list contains destroyed object" );
    }
    // Sync up local propagation epoch with the global one. Release fence prevents 
    // reordering of possible store to *mptr_state after the sync point.
    __TBB_store_with_release(my_context_state_propagation_epoch, the_context_state_propagation_epoch);
}

template <typename T>
bool market::propagate_task_group_state ( T task_group_context::*mptr_state, task_group_context& src, T new_state ) {
    if ( !(src.my_state & task_group_context::may_have_children) )
        return true;
    // The whole propagation algorithm is under the lock in order to ensure correctness 
    // in case of concurrent state changes at the different levels of the context tree.
    // See the note 3 at the bottom of scheduler.cpp
    spin_mutex::scoped_lock lock(the_context_state_propagation_mutex);
    if ( src.*mptr_state != new_state )
        // Another thread has concurrently changed the state. Back off.
        return false;
    src.*mptr_state = new_state;
    // Advance global state propagation epoch
    __TBB_FetchAndAddWrelease(&the_context_state_propagation_epoch, 1);
    // Propagate to all workers and masters and sync up their local epochs with the global one
    unsigned num_workers = my_num_workers;
    for ( unsigned i = 0; i < num_workers; ++i ) {
        generic_scheduler *s = my_workers[i];
        // If the worker is only about to be registered, skip it.
        if ( s )
            s->propagate_task_group_state( mptr_state, new_state );
    }
    // Propagate to all master threads (under my_arenas_list_mutex lock)
    ForEachArena(a) {
        arena_slot &slot = a.my_slots[0];
        generic_scheduler *s = slot.my_scheduler;
        // If the master is under construction, skip it. Otherwise make sure that it does not 
        // leave its arena and its scheduler get destroyed while we accessing its data.
        if ( s && __TBB_CompareAndSwapW(&slot.my_scheduler, (intptr_t)LockedMaster, (intptr_t)s) == (intptr_t)s ) {
            __TBB_ASSERT( slot.my_scheduler == LockedMaster, NULL );
            // The whole propagation sequence is locked, thus no contention is expected
            __TBB_ASSERT( s != LockedMaster, NULL );
            s->propagate_task_group_state( mptr_state, new_state );
            __TBB_store_with_release( slot.my_scheduler, s );
        }
    } EndForEach();
    return true;
}

template <typename T>
bool arena::propagate_task_group_state ( T task_group_context::*mptr_state, task_group_context& src, T new_state ) {
    return my_market->propagate_task_group_state( mptr_state, src, new_state );
}
#endif /* __TBB_TASK_GROUP_CONTEXT */

bool task_group_context::cancel_group_execution () {
    __TBB_ASSERT ( my_cancellation_requested == 0 || my_cancellation_requested == 1, "Invalid cancellation state");
    if ( my_cancellation_requested || __TBB_CompareAndSwapW(&my_cancellation_requested, 1, 0) ) {
        // This task group has already been canceled
        return false;
    }
    governor::local_scheduler()->my_arena->propagate_task_group_state( &task_group_context::my_cancellation_requested, *this, (uintptr_t)1 );
    return true;
}

bool task_group_context::is_group_execution_cancelled () const {
    return my_cancellation_requested != 0;
}

// IMPORTANT: It is assumed that this method is not used concurrently!
void task_group_context::reset () {
    //! \todo Add assertion that this context does not have children
    // No fences are necessary since this context can be accessed from another thread
    // only after stealing happened (which means necessary fences were used).
    if ( my_exception )  {
        my_exception->destroy();
        my_exception = NULL;
    }
    my_cancellation_requested = 0;
}

void task_group_context::register_pending_exception () {
    if ( my_cancellation_requested )
        return;
#if TBB_USE_EXCEPTIONS
    try {
        throw;
    } TbbCatchAll( this );
#endif /* TBB_USE_EXCEPTIONS */
}

#if __TBB_TASK_PRIORITY
void task_group_context::set_priority ( priority_t prio ) {
    __TBB_ASSERT( prio == priority_low || prio == priority_normal || prio == priority_high, "Invalid priority level value" );
    intptr_t p = normalize_priority(prio);
    if ( my_priority == p )
        return;
    my_priority = p;
    internal::generic_scheduler* s = governor::local_scheduler_if_initialized();
    if ( !s || !s->my_arena->propagate_task_group_state(&task_group_context::my_priority, *this, p) )
        return;
    // Updating arena priority here does not eliminate necessity of checking each
    // task priority and updating arena priority if necessary before the task execution.
    // These checks will be necessary because:
    // a) set_priority() may be invoked before any tasks from this task group are spawned;
    // b) all spawned tasks from this task group are retrieved from the task pools.
    // These cases create a time window when arena priority may be lowered.
    s->my_market->update_arena_priority( *s->my_arena, p );
}

priority_t task_group_context::priority () const {
    return static_cast<priority_t>(priority_from_normalized_rep[my_priority]);
}
#endif /* __TBB_TASK_PRIORITY */

#endif /* __TBB_TASK_GROUP_CONTEXT */

} // namespace tbb
