<script lang="ts">
  import { page } from '$app/stores';
  import LockCloseIcon from '$lib/icons/lock-close.svelte';
  import ProfileIcon from '$lib/icons/profile.svelte';
</script>

<section class="py-10 md:py-16">
  <div class="container">
    <div class="flex flex-col gap-2">
      <div class="tabs-boxed tabs flex justify-center bg-base-100">
        <a
          href="/profile/settings/personal"
          class="tab"
          class:tab-active={$page.url.pathname.includes('/personal')}
        >
          <ProfileIcon /> Personal Information
        </a>
        <a
          href="/profile/settings/security"
          class="tab"
          class:tab-active={$page.url.pathname.includes('/security')}
        >
          <LockCloseIcon /> Security
        </a>
      </div>
      <slot />
    </div>
  </div>
</section>
