<script lang="ts">
  import { enhance } from '$app/forms';
  import type { ActionData } from './$types';

  export let form: ActionData;
</script>

<div class="hero min-h-screen">
  <div class="hero-content text-center">
    <div class="max-w-md">
      <form class="flex flex-col gap-4" method="POST" action="?/forgotPassword" use:enhance>
        <div class="form-control w-full max-w-xs">
          <label for="email" class="label">
            <span class="label-text">Email</span>
          </label>
          <input
            type="email"
            id="email"
            name="email"
            class="input-bordered input w-full max-w-xs"
          />
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

        <button class="btn-primary btn">Send</button>
      </form>
    </div>
  </div>
</div>
