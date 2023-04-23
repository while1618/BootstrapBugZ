<script lang="ts">
  export let openModal = false;
  export let title = '';
  export let closeButtonText = '';
</script>

<div class="modal modal-bottom sm:modal-middle" class:modal-open={openModal}>
  <div class="modal-box">
    <h3 class="text-lg font-bold">{title}</h3>
    <slot name="body" />
    <div class="modal-action">
      <slot name="confirmAction" />
      <button class="btn" on:click={() => (openModal = false)}>{closeButtonText}</button>
    </div>
  </div>
</div>
