<script lang="ts">
  import * as m from '$lib/paraglide/messages.js';
  import { userStore } from '$lib/stores/user';
</script>

<section>
  <div class="container">
    <div class="hero h-96 overflow-hidden rounded-box bg-primary md:h-[500px]">
      <div class="hero-overlay"></div>
      <div class="hero-content text-center">
        <div class="max-w-lg">
          <h1 class="mb-5 text-4xl font-bold sm:mb-7 sm:text-5xl">
            {m.home_greetings()}, {$userStore?.username}
          </h1>
          <p class="mb-5 sm:mb-7 sm:text-lg">{m.home_info()}</p>
          <a href="/" class="btn btn-accent sm:btn-wide">{m.home_getStarted()}</a>
        </div>
      </div>
    </div>
  </div>
</section>
