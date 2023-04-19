<script lang="ts">
  import { createEventDispatcher } from 'svelte';

  export let open = false;
  export let title = '';
  const dispatch = createEventDispatcher();
</script>

<div class="modal" class:modal-open={open}>
  <div class="modal-box">
    <h3 class="text-lg font-bold">{title}</h3>
    <slot name="body" />
    <div class="modal-action">
      <slot name="confirmAction" />
      <button class="btn text-error" on:click={() => dispatch('close')}>Cancel</button>
    </div>
  </div>
</div>
