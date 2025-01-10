import type { NextConfig } from 'next';
import nextra from 'nextra';

const nextConfig: NextConfig = {
  /* config options here */
  reactStrictMode: true,
};

const withNextra = nextra({
  theme: 'nextra-theme-docs',
  themeConfig: './theme.config.tsx',
});

export default withNextra(nextConfig);
