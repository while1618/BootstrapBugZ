<script lang="ts">
  import { enhance } from '$app/forms';
  import FormControl from '$lib/components/form-control.svelte';
  import FormErrors from '$lib/components/form-errors.svelte';
  import type { ActionData } from './$types';

  export let form: ActionData;
</script>

<div class="flex h-screen items-center justify-center">
  <div class="card mx-auto w-full max-w-xl bg-base-100 p-8 shadow-xl">
    <div class="flex flex-col gap-8">
      <h1 class="text-center text-3xl font-bold">Forgot password</h1>
      <form class="flex flex-col gap-4" method="POST" action="?/forgotPassword" use:enhance>
        <FormControl {form} type="email" id="email" label="Email" />
        <FormErrors {form} />
        <button class="btn btn-primary">Send</button>
      </form>
    </div>
  </div>
</div>
