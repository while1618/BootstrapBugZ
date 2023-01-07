<script>
  import { Button, Card, FloatingLabelInput, TabItem, Tabs } from 'flowbite-svelte';
</script>

<div class="flex justify-center pt-10">
  <Card size="md" class="w-full">
    <Tabs style="underline" class="justify-center">
      <TabItem open title="Update profile">
        <form class="flex flex-col space-y-6" action="/">
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
          <Button type="submit" class="w-full">Update</Button>
        </form>
      </TabItem>
      <TabItem title="Change password">
        <form class="flex flex-col space-y-6" action="/">
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
      </TabItem>
      <TabItem title="Logged in devices">
        <Button type="submit" class="w-full">Sign out from all devices</Button>
      </TabItem>
    </Tabs>
  </Card>
</div>

<style>
</style>
