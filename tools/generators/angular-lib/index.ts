import { formatFiles, generateFiles, installPackagesTask, Tree } from '@nrwl/devkit';
import { wrapAngularDevkitSchematic } from '@nrwl/tao/src/commands/ngcli-adapter';
import { stringUtils } from '@nrwl/workspace';
import * as path from 'path';

export const libraryGenerator = wrapAngularDevkitSchematic('@nrwl/angular', 'library');

export interface SchematicOptions {
  name: string;
}

const deleteRootEslintFile = async (tree: Tree) => {
  tree.delete('.eslintrc.json');
};

const replaceEslintFileInLib = async (tree: Tree, options: SchematicOptions) => {
  const libPath = path.join('libs', stringUtils.dasherize(options.name).replace(/\\/g, '/'));
  const filesPath = path.join(__dirname, './files');
  const substitutions = {
    libPath,
    relativePathFromLibToRoot: path.relative(path.dirname(libPath), path.dirname(tree.root)),
  };
  tree.delete(`${libPath}/.eslintrc.json`);
  generateFiles(tree, filesPath, libPath, substitutions);
};

export default async (tree: Tree, options: SchematicOptions) => {
  await libraryGenerator(tree, options);
  await deleteRootEslintFile(tree);
  await replaceEslintFileInLib(tree, options);
  await formatFiles(tree);
  return () => {
    installPackagesTask(tree);
  };
};
