import type { DocsThemeConfig } from 'nextra-theme-docs';
import { useConfig } from 'nextra-theme-docs';

const config: DocsThemeConfig = {
  logo: <span>bugzkit</span>,
  project: {
    link: 'https://github.com/while1618/bugzkit',
  },
  docsRepositoryBase: 'https://github.com/while1618/bugzkit/tree/master/docs',
  head: function Head() {
    const config = useConfig();
    const title = `${config.title} | bugzkit`;

    return (
      <>
        <title>{title}</title>
      </>
    );
  },
  footer: {
    content: (
      <span>
        {new Date().getFullYear()} ©{' '}
        <a href="https://github.com/while1618/bugzkit" target="_blank">
          bugzkit
        </a>
        .
      </span>
    ),
  },
};

export default config;
