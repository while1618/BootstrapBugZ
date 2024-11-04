<script lang="ts">
  interface Props {
    /* eslint-disable @typescript-eslint/no-explicit-any */
    form: any;
    type: string;
    name: string;
    label: string;
    value?: any;
    /* eslint-enable */
  }

  let { form, type, name, label, value }: Props = $props();
</script>

<div class="form-control w-full">
  <label for={name} class="label">
    <span class="label-text">{label}</span>
  </label>
  <input {type} id={name} {name} {value} class="input input-bordered bg-base-200 w-full" />
  {#if form?.errors && form?.errors[name]}
    <label for={name} class="label">
      <span class="label-text text-error">{form.errors[name][0]}</span>
    </label>
  {/if}
</div>
