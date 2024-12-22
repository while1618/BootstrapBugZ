<script lang="ts">
  import { page } from '$app/stores';
  import { Button } from '$lib/components/ui/button';
  import * as m from '$lib/paraglide/messages.js';
  import ChevronLeftIcon from 'lucide-svelte/icons/chevron-left';
  import ChevronRightIcon from 'lucide-svelte/icons/chevron-right';
  import EllipsisIcon from 'lucide-svelte/icons/ellipsis';

  interface Props {
    currentPage: number;
    totalPages: number;
    size: number;
  }

  const { currentPage, totalPages, size }: Props = $props();
  const previousDisabled = currentPage <= 1;
  const nextDisabled = currentPage >= totalPages;
</script>

<div class="flex gap-1">
  <Button
    variant="ghost"
    disabled={previousDisabled}
    href={!previousDisabled ? `${$page.route.id}?page=${currentPage - 1}&size=${size}` : undefined}
  >
    <ChevronLeftIcon />
    {m.general_previous()}
  </Button>

  {#if currentPage > 2}
    <Button variant="ghost" href="{$page.route.id}?page=1&size={size}">1</Button>
    <EllipsisIcon class="size-4 self-center" />
  {/if}

  {#if currentPage - 1 > 0}
    <Button variant="ghost" href="{$page.route.id}?page={currentPage - 1}&size={size}">
      {currentPage - 1}
    </Button>
  {/if}

  <Button variant="outline">{currentPage}</Button>

  {#if currentPage + 1 <= totalPages}
    <Button variant="ghost" href="{$page.route.id}?page={currentPage + 1}&size={size}">
      {currentPage + 1}
    </Button>
  {/if}

  {#if currentPage < totalPages - 1}
    <EllipsisIcon class="size-4 self-center" />
    <Button variant="ghost" href="{$page.route.id}?page={totalPages}&size={size}">
      {totalPages}
    </Button>
  {/if}

  <Button
    variant="ghost"
    disabled={nextDisabled}
    href={!nextDisabled ? `${$page.route.id}?page=${currentPage + 1}&size=${size}` : undefined}
  >
    {m.general_next()}
    <ChevronRightIcon />
  </Button>
</div>
