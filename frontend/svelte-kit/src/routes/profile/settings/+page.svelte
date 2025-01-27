<script lang="ts">
  import * as Tabs from '$lib/components/ui/tabs/index.js';
  import * as m from '$lib/paraglide/messages.js';
  import type { PageProps } from './$types';
  import PersonalInformation from './(components)/personal-information.svelte';
  import Security from './(components)/security.svelte';

  const { data }: PageProps = $props();
</script>

<section>
  <div class="container">
    <div class="m-20 flex justify-center">
      <Tabs.Root value="personalInformation">
        <Tabs.List class="grid grid-cols-2">
          <Tabs.Trigger value="personalInformation">
            {m.profile_personalInformation()}
          </Tabs.Trigger>
          <Tabs.Trigger value="security">
            {m.profile_security()}
          </Tabs.Trigger>
        </Tabs.List>
        <Tabs.Content value="personalInformation">
          <PersonalInformation {data} />
        </Tabs.Content>
        <Tabs.Content value="security">
          <Security {data} />
        </Tabs.Content>
      </Tabs.Root>
    </div>
  </div>
</section>
