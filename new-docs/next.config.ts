import type { NextConfig } from 'next';
import nextra from 'nextra';

const nextConfig: NextConfig = {
  output: 'export',
  images: {
    unoptimized: true,
  },
};

const withNextra = nextra({});

export default withNextra(nextConfig);
