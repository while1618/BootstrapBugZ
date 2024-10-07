<script lang="ts">
  /* eslint-disable @typescript-eslint/no-explicit-any */
  export let form: any;
  export let type: string;
  export let name: string;
  export let label: string;
  export let value: any = null;
  /* eslint-enable */
</script>

<div class="form-control w-full">
  <label for={name} class="label">
    <span class="label-text">{label}</span>
  </label>
  <input {type} id={name} {name} {value} class="input input-bordered w-full bg-base-200" />
  {#if form?.errors && form?.errors[name]}
    <label for={name} class="label">
      <span class="label-text text-error">{form.errors[name][0]}</span>
    </label>
  {/if}
</div>
