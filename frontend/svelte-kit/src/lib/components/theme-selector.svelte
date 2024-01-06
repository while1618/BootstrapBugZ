<script lang="ts">
  import MoonIcon from '$lib/icons/moon.svelte';
  import SunIcon from '$lib/icons/sun.svelte';
</script>

<label class="swap swap-rotate">
  <input type="checkbox" class="theme-controller" value="light" />
  <SunIcon />
  <MoonIcon />
</label>
