import type { PlaywrightTestConfig } from '@playwright/test';

const config: PlaywrightTestConfig = {
  webServer: {
    command: 'pnpm run build && pnpm run preview',
    port: 4173,
  },
  use: {
    browserName: 'firefox',
  },
  testDir: 'tests',
  testMatch: /(.+\.)?(test|spec)\.[jt]s/,
};

export default config;
