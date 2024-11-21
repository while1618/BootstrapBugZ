<script lang="ts">
  import { page as pageStore } from '$app/stores';
  import { Button } from '$lib/components/ui/button';
  import * as Pagination from '$lib/components/ui/pagination';
  import * as m from '$lib/paraglide/messages.js';
  import { ChevronLeftIcon, ChevronRightIcon } from 'lucide-svelte';

  interface Props {
    count: number;
    page: number;
    size: number;
  }

  const { count, page, size }: Props = $props();
</script>

<Pagination.Root {page} {count} perPage={size}>
  {#snippet children({ pages, currentPage })}
    <Pagination.Content>
      <Pagination.Item>
        <Pagination.PrevButton>
          {#snippet child()}
            <Button variant="ghost" href="{$pageStore.route.id}?page={currentPage - 1}&size={size}">
              <ChevronLeftIcon />
              {m.general_previous()}
            </Button>
          {/snippet}
        </Pagination.PrevButton>
      </Pagination.Item>
      {#each pages as p (p.key)}
        {#if p.type === 'ellipsis'}
          <Pagination.Item>
            <Pagination.Ellipsis />
          </Pagination.Item>
        {:else}
          <Pagination.Item>
            <Pagination.Link page={p} isActive={currentPage === p.value}>
              {#snippet children()}
                <Button variant="ghost" href="{$pageStore.route.id}?page={p.value}&size={size}">
                  {p.value}
                </Button>
              {/snippet}
            </Pagination.Link>
          </Pagination.Item>
        {/if}
      {/each}
      <Pagination.Item>
        <Pagination.NextButton>
          {#snippet child()}
            <Button variant="ghost" href="{$pageStore.route.id}?page={currentPage + 1}&size={size}">
              {m.general_next()}
              <ChevronRightIcon />
            </Button>
          {/snippet}
        </Pagination.NextButton>
      </Pagination.Item>
    </Pagination.Content>
  {/snippet}
</Pagination.Root>
