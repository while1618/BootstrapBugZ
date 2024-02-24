import type { User } from '$lib/models/user/user';
import { writable } from 'svelte/store';

export const userStore = writable<User | null>(null);
