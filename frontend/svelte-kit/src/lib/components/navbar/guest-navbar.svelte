<script lang="ts">
  import { Button } from '$lib/components/ui/button';
  import * as Sheet from '$lib/components/ui/sheet';
  import * as m from '$lib/paraglide/messages.js';
  import { MenuIcon } from 'lucide-svelte';
  import LanguageSwitcher from './language-switcher.svelte';
  import ThemeSelector from './theme-selector.svelte';
</script>

<header class="sticky top-0 z-50 py-2">
  <div class="container">
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-3 lg:hidden">
        <Sheet.Root>
          <Sheet.Trigger>
            <MenuIcon />
            <span class="sr-only">Toggle Menu</span>
          </Sheet.Trigger>
          <Sheet.Content side="left">
            <Sheet.Header>
              <Sheet.Description>
                <div class="flex flex-col items-start gap-2">
                  <LanguageSwitcher />
                  <Button href="/auth/sign-in" variant="ghost" class="w-full justify-start">
                    {m.navbar_singIn()}
                  </Button>
                  <Button href="/auth/sign-up" variant="ghost" class="w-full justify-start">
                    {m.navbar_signUp()}
                  </Button>
                </div>
              </Sheet.Description>
            </Sheet.Header>
          </Sheet.Content>
        </Sheet.Root>
      </div>

      <Button href="/" class="text-2xl" variant="ghost">BootstrapBugZ</Button>

      <div class="ml-auto flex items-center gap-3">
        <div class="flex gap-3 lg:hidden">
          <ThemeSelector />
        </div>

        <div class="hidden gap-3 lg:flex">
          <Button href="/auth/sign-in" variant="ghost">{m.navbar_singIn()}</Button>
          <Button href="/auth/sign-up" variant="ghost">{m.navbar_signUp()}</Button>
          <ThemeSelector />
          <LanguageSwitcher />
        </div>
      </div>
    </div>
  </div>
</header>
