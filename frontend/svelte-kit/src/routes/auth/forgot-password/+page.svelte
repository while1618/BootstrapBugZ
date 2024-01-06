<script lang="ts">
  import { enhance } from '$app/forms';
  import type { ActionData } from './$types';

  export let form: ActionData;
</script>

<div class="flex h-screen items-center justify-center">
  <div class="card mx-auto w-full max-w-xl bg-base-100 p-8 shadow-xl">
    <div class="flex flex-col gap-10">
      <h1 class="text-center text-3xl font-bold">Forgot password</h1>
      <form class="flex flex-col gap-4" method="POST" action="?/forgotPassword" use:enhance>
        <div class="form-control w-full">
          <label for="email" class="label">
            <span class="label-text">Email</span>
          </label>
          <input type="email" id="email" name="email" class="input input-bordered w-full" />
          {#if form?.errors?.email}
            <label for="email" class="label">
              <span class="label-text text-error">{form.errors.email[0]}</span>
            </label>
          {/if}
        </div>

        {#if form?.errorMessage}
          {#each form.errorMessage.details as error}
            <div class="flex">
              <p class="label-text text-error">{error.message}</p>
            </div>
          {/each}
        {/if}

        <button class="btn btn-primary">Send</button>
      </form>
    </div>
  </div>
</div>
