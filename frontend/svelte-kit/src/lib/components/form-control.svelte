<script lang="ts">
  /* eslint-disable @typescript-eslint/no-explicit-any */
  export let form: any;
  export let type: string;
  export let id: string;
  export let label: string;
  export let value: any = null;
  /* eslint-enable */
</script>

<div class="form-control w-full">
  <label for={id} class="label">
    <span class="label-text">{label}</span>
  </label>
  <input {type} {id} name={id} {value} class="input input-bordered w-full" />
  {#if form?.errors && form?.errors[id]}
    <label for={id} class="label">
      <span class="label-text text-error">{form.errors[id][0]}</span>
    </label>
  {/if}
</div>
