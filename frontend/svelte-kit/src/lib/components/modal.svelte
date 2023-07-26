<script lang="ts">
  export let open = false;
  export let title = '';

  function closeModal(): void {
    open = false;
  }

  function handleKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Escape') closeModal();
  }
</script>

<svelte:window on:keydown={handleKeyDown} />

<div
  class="modal modal-bottom sm:modal-middle"
  class:modal-open={open}
  on:click|self={closeModal}
  on:keydown={handleKeyDown}
  tabindex="0"
  role="button"
>
  <div class="modal-box">
    <h3 class="text-lg font-bold">{title}</h3>
    <slot name="body" />
    <div class="modal-action">
      <slot name="actions" />
    </div>
  </div>
</div>
