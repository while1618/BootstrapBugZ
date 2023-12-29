<script lang="ts">
  import { page } from '$app/stores';
  import LockCloseIcon from '$lib/icons/lock-close.svelte';
  import ProfileIcon from '$lib/icons/profile.svelte';
</script>

<div class="tabs-boxed tabs flex justify-center">
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
