<script>
  import { Button, Card, FloatingLabelInput, TabItem, Tabs } from 'flowbite-svelte';
</script>

<div class="flex justify-center pt-10">
  <Card
    size="md"
    color="none"
    class="border-gray-100 bg-white dark:border-gray-700 dark:bg-gray-800"
  >
    <Tabs style="underline" class="justify-center">
      <TabItem open title="Update profile">
        <div class="flex justify-center">
          <form class="flex w-2/3 flex-col space-y-6">
            <FloatingLabelInput
              class="space-y-2"
              style="standard"
              id="firstName"
              name="firstName"
              type="text"
              label="First name"
            />
            <FloatingLabelInput
              class="space-y-2"
              style="standard"
              id="lastName"
              name="lastName"
              type="text"
              label="Last name"
            />
            <FloatingLabelInput
              class="space-y-2"
              style="standard"
              id="username"
              name="username"
              type="text"
              label="Username"
            />
            <FloatingLabelInput
              class="space-y-2"
              style="standard"
              id="email"
              name="email"
              type="email"
              label="Email"
            />
            <Button type="submit">Update</Button>
          </form>
        </div>
      </TabItem>
      <TabItem title="Change password">
        <div class="flex justify-center">
          <form class="flex w-2/3 flex-col space-y-6">
            <FloatingLabelInput
              class="space-y-2"
              style="standard"
              id="password"
              name="password"
              type="password"
              label="Password"
            />
            <FloatingLabelInput
              class="space-y-2"
              style="standard"
              id="confirmPassword"
              name="confirmPassword"
              type="password"
              label="Confirm password"
            />
            <Button type="submit">Change password</Button>
          </form>
        </div>
      </TabItem>
      <TabItem title="Logged in devices">
        <div class="flex justify-center">
          <Button type="submit" class="w-2/3">Sign out from all devices</Button>
        </div>
      </TabItem>
    </Tabs>
  </Card>
</div>

<style>
</style>
