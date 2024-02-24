<script lang="ts">
  export let dialog: HTMLDialogElement;
  export let title = '';
</script>

<dialog class="modal modal-bottom sm:modal-middle" bind:this={dialog}>
  <div class="modal-box">
    <h3 class="text-lg font-bold">{title}</h3>
    <slot name="body" />
    <div class="modal-action">
      <slot name="actions" />
    </div>
  </div>
</dialog>
