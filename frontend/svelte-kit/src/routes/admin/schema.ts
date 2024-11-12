import { z } from 'zod';

export const adminSchema = z.object({});

export const roleSchema = z.object({
  names: z.string().array(),
});
