import { Tree } from '@nrwl/devkit';
import { wrapAngularDevkitSchematic } from '@nrwl/tao/src/commands/ngcli-adapter';
import { Schema } from '@nrwl/tao/src/shared/params';

export const libraryGenerator = wrapAngularDevkitSchematic('@nrwl/angular', 'library');

export default async function (tree: Tree, options: Schema) {
  await libraryGenerator(tree, options);
}
