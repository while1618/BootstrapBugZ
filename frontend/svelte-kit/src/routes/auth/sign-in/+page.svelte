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
      <h1 class="text-center text-3xl font-bold">Sign in</h1>
      <form class="flex flex-col gap-4" method="POST" action="?/signIn" use:enhance>
        <FormControl {form} type="text" id="usernameOrEmail" label="Username or email" />
        <FormControl {form} type="password" id="password" label="Password" />
        <FormErrors {form} />
        <button class="btn btn-primary">Sign in</button>
        <div class="flex gap-4">
          <span class="label-text">
            Don't have an account? <a href="/auth/sign-up" class="text-info hover:underline">
              Sign up
            </a>
          </span>
          <a href="/auth/forgot-password" class="label-text ml-auto text-info hover:underline">
            Forgot password?
          </a>
        </div>
      </form>
    </div>
  </div>
</div>
