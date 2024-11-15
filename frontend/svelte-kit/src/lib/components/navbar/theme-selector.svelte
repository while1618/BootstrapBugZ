<script lang="ts">
  import { Button } from '$lib/components/ui/button';
  import { MoonIcon, SunIcon } from 'lucide-svelte';
  import { onMount } from 'svelte';

  let currentTheme = $state('dark');

  onMount(() => {
    currentTheme =
      document.cookie
        .split('; ')
        .find((row) => row.startsWith('theme'))
        ?.split('=')[1] ?? 'dark';
    document.documentElement.setAttribute('class', currentTheme);
  });

  function changeTheme() {
    currentTheme = currentTheme === 'dark' ? '' : 'dark';
    document.cookie = `theme=${currentTheme}; max-age=${60 * 60 * 24 * 365}; path=/; SameSite=Strict;`;
    document.documentElement.setAttribute('class', currentTheme);
  }
</script>

<Button onclick={changeTheme} variant="ghost" size="icon">
  <SunIcon
    class="h-[1.2rem] w-[1.2rem] rotate-0 scale-100 transition-all dark:-rotate-90 dark:scale-0"
  />
  <MoonIcon
    class="absolute h-[1.2rem] w-[1.2rem] rotate-90 scale-0 transition-all dark:rotate-0 dark:scale-100"
  />
  <span class="sr-only">Change theme</span>
</Button>
