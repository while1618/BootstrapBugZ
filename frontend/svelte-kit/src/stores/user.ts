import type { UserDTO } from '$lib/models/user';
import { writable } from 'svelte/store';

export const signedInUser = writable<UserDTO | null>(null);
