import type { UserDTO } from '$lib/models/user/user';
import { writable } from 'svelte/store';

export const userStore = writable<UserDTO | null>(null);
