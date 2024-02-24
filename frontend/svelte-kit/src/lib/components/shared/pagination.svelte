<script lang="ts">
  import { page } from '$app/stores';

  export let currentPage: number;
  export let totalPages: number;
  export let size: number;
</script>

<div class="gap-2">
  {#if currentPage > 1}
    <a class="btn btn-ghost" href="{$page.url.pathname}?page={currentPage - 1}&size={size}">
      Previous
    </a>
  {/if}
  {#each [2, 1] as i}
    {#if currentPage - i > 0}
      <a class="btn btn-ghost" href="{$page.url.pathname}?page={currentPage - i}&size={size}">
        {currentPage - i}
      </a>
    {/if}
  {/each}
  <span class="btn btn-primary btn-active">{currentPage}</span>
  {#each Array(2) as _, i}
    {#if currentPage + (i + 1) <= totalPages}
      <a class="btn btn-ghost" href="{$page.url.pathname}?page={currentPage + (i + 1)}&size={size}">
        {currentPage + (i + 1)}
      </a>
    {/if}
  {/each}
  {#if currentPage < totalPages}
    <a class="btn btn-ghost" href="{$page.url.pathname}?page={currentPage + 1}&size={size}">Next</a>
  {/if}
</div>
