import { test } from '@playwright/test';

test('go to home page', async ({ page }) => {
  await page.goto('/');
});
