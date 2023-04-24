import type { PlaywrightTestConfig } from '@playwright/test';

const config: PlaywrightTestConfig = {
  webServer: {
    command: 'pnpm run build && pnpm run preview',
    port: 4173,
  },
  testDir: 'tests',
  use: {
    browserName: 'firefox',
  },
};

export default config;
