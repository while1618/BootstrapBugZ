<script lang="ts">
  import type { PageData } from './$types';

  export let data: PageData;
</script>

<div class="flex flex-grow justify-center p-10">
  <h1 class="text-lg dark:text-white">{data.name}</h1>
</div>

<style>
</style>
