<script lang="ts" module>
  type T = Record<string, unknown>;
</script>

<script lang="ts" generics="T extends Record<string, unknown>">
  import { formFieldProxy, type FormPathLeaves, type SuperForm } from 'sveltekit-superforms';

  interface Props {
    type: string;
    label: string;
    superform: SuperForm<T>;
    field: FormPathLeaves<T>;
  }

  const { type, label, superform, field }: Props = $props();
  const { value, errors, constraints } = formFieldProxy(superform, field);
</script>

<div class="form-control w-full">
  <label for={field} class="label">
    <span class="label-text">{label}</span>
  </label>
  <input
    {type}
    name={field}
    aria-invalid={$errors ? 'true' : undefined}
    bind:value={$value}
    {...$constraints}
    class="input input-bordered w-full bg-base-200"
  />
  {#if $errors}
    <label for={field} class="label">
      <span class="label-text text-error">{$errors}</span>
    </label>
  {/if}
</div>
