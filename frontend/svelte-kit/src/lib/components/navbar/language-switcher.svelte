<script lang="ts">
  import { goto } from '$app/navigation';
  import { page } from '$app/stores';
  import { i18n } from '$lib/i18n.js';
  import * as m from '$lib/paraglide/messages.js';
  import { availableLanguageTags, languageTag } from '$lib/paraglide/runtime';
  import { get } from 'svelte/store';

  type Language = 'en' | 'sr';

  const labels = {
    en: `🇬🇧 ${m.language_english()}`,
    sr: `🇷🇸 ${m.language_serbian()}`,
  };

  const changeLanguage = (target: EventTarget & HTMLSelectElement) => {
    const selectedLanguage = target.value as Language;
    const canonicalPath = i18n.route(get(page).url.pathname);
    const localizedPath = i18n.resolveRoute(canonicalPath, selectedLanguage);
    goto(localizedPath);
  };
</script>

<select
  class="select select-info w-36 max-w-xs border-none"
  on:change={(event) => changeLanguage(event.currentTarget)}
>
  {#each availableLanguageTags as lang}
    <option value={lang} selected={lang === languageTag()}>{labels[lang]}</option>
  {/each}
</select>
