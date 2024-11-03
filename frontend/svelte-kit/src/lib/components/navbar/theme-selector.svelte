<script lang="ts">
  import MoonIcon from '$lib/icons/moon.svelte';
  import SunIcon from '$lib/icons/sun.svelte';
  import { onMount } from 'svelte';

  const defaultTheme = 'dark';
  let currentTheme = '';

  onMount(() => {
    const theme =
      document.cookie
        .split('; ')
        .find((row) => row.startsWith('theme'))
        ?.split('=')[1] ?? defaultTheme;
    currentTheme = theme;
    document.documentElement.setAttribute('data-theme', theme);
  });

  function changeTheme() {
    const changeTo = currentTheme === 'dark' ? 'cupcake' : 'dark';
    currentTheme = changeTo;
    document.cookie = `theme=${changeTo}; max-age=${60 * 60 * 24 * 365}; path=/; SameSite=Strict;`;
    document.documentElement.setAttribute('data-theme', changeTo);
  }
</script>

<label class="swap swap-rotate">
  <input type="checkbox" class="theme-controller" onclick={changeTheme} />
  <SunIcon />
  <MoonIcon />
</label>
