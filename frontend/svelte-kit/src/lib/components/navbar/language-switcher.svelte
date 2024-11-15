<script lang="ts">
  import { goto } from '$app/navigation';
  import { page } from '$app/stores';
  import * as Select from '$lib/components/ui/select';
  import { i18n } from '$lib/i18n.js';
  import * as m from '$lib/paraglide/messages.js';
  import { availableLanguageTags, languageTag } from '$lib/paraglide/runtime';
  import { get } from 'svelte/store';

  const labels = {
    en: `🇬🇧 ${m.language_english()}`,
    sr: `🇷🇸 ${m.language_serbian()}`,
  };

  let selectedLanguage = $state(languageTag());

  const changeLanguage = () => {
    const canonicalPath = i18n.route(get(page).url.pathname);
    const localizedPath = i18n.resolveRoute(canonicalPath, selectedLanguage);
    goto(localizedPath);
  };
</script>

<Select.Root type="single" bind:value={selectedLanguage} onValueChange={changeLanguage}>
  <Select.Trigger class="w-[180px]">{labels[selectedLanguage]}</Select.Trigger>
  <Select.Content>
    <Select.Group>
      <Select.GroupHeading>{m.languages()}</Select.GroupHeading>
      {#each availableLanguageTags as lang}
        <Select.Item value={lang} label={labels[lang]} />
      {/each}
    </Select.Group>
  </Select.Content>
</Select.Root>
