<script lang="ts">
  interface Props {
    dialog: HTMLDialogElement;
    title: string;
    body?: import('svelte').Snippet;
    actions?: import('svelte').Snippet;
  }

  let { dialog = $bindable(), title, body, actions }: Props = $props();
</script>

<dialog class="modal modal-bottom sm:modal-middle" bind:this={dialog}>
  <div class="modal-box">
    <h3 class="text-lg font-bold">{title}</h3>
    {@render body?.()}
    <div class="modal-action">
      {@render actions?.()}
    </div>
  </div>
</dialog>
