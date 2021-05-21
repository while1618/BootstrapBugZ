import { formatFiles, generateFiles, installPackagesTask, Tree } from '@nrwl/devkit';
import { libraryGenerator, stringUtils } from '@nrwl/workspace';
import * as path from 'path';

export interface SchematicOptions {
  name: string;
}

export default async function (tree: Tree, options: SchematicOptions) {
  await libraryGenerator(tree, options);
  await deleteRootEslintFile(tree);
  await replaceEslintFileInLib(tree, options);
  await formatFiles(tree);
  return () => {
    installPackagesTask(tree);
  };
}

async function deleteRootEslintFile(tree: Tree) {
  tree.delete('.eslintrc.json');
}

async function replaceEslintFileInLib(tree: Tree, options: SchematicOptions) {
  const libPath = path.join('libs', stringUtils.dasherize(options.name));
  const filesPath = path.join(__dirname, './files');
  const substitutions = {
    libPath,
    relativePathFromLibToRoot: path.relative(path.dirname(libPath), path.dirname(tree.root)),
  };
  tree.delete(`${libPath}/.eslintrc.json`);
  generateFiles(tree, filesPath, libPath, substitutions);
}
