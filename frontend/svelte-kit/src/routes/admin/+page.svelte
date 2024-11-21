<script lang="ts">
  import Pagination from '$lib/components/shared/pagination.svelte';
  import { Label } from '$lib/components/ui/label';
  import * as Table from '$lib/components/ui/table';
  import * as m from '$lib/paraglide/messages.js';
  import type { PageServerData } from './$types';
  import ActivateDialog from './(components)/activate-dialog.svelte';
  import DeleteDialog from './(components)/delete-dialog.svelte';
  import LockDialog from './(components)/lock-dialog.svelte';
  import RoleDialog from './(components)/role-dialog.svelte';

  interface Props {
    data: PageServerData;
  }

  const { data }: Props = $props();
</script>

<section>
  <div class="container">
    <div class="m-10 flex flex-col gap-5">
      <Table.Root>
        <Table.Header>
          <Table.Row>
            <Table.Head>{m.admin_userId()}</Table.Head>
            <Table.Head>{m.admin_userUsername()}</Table.Head>
            <Table.Head>{m.admin_userEmail()}</Table.Head>
            <Table.Head>{m.admin_userCreatedAt()}</Table.Head>
            <Table.Head>{m.admin_userActive()}</Table.Head>
            <Table.Head>{m.admin_userLock()}</Table.Head>
            <Table.Head>{m.admin_userRoles()}</Table.Head>
          </Table.Row>
        </Table.Header>
        <Table.Body>
          {#each data.users.data as user}
            <Table.Row>
              <Table.Cell>{user.id}</Table.Cell>
              <Table.Cell>{user.username}</Table.Cell>
              <Table.Cell>{user.email}</Table.Cell>
              <Table.Cell>{new Date(user.createdAt).toLocaleString()}</Table.Cell>
              <Table.Cell><ActivateDialog {data} {user} /></Table.Cell>
              <Table.Cell><LockDialog {data} {user} /></Table.Cell>
              <Table.Cell>
                {#if user.roles}
                  <div class="flex items-center gap-2">
                    {#each user.roles as role, i}
                      <Label>
                        {role.name}
                        {#if i < user.roles.length - 1},{/if}
                      </Label>
                    {/each}
                    <RoleDialog {data} {user} />
                  </div>
                {/if}
              </Table.Cell>
              <Table.Cell><DeleteDialog {data} {user} /></Table.Cell>
            </Table.Row>
          {/each}
        </Table.Body>
      </Table.Root>
      <Pagination count={data.users.total} page={data.pageable.page} size={data.pageable.size} />
    </div>
  </div>
</section>
