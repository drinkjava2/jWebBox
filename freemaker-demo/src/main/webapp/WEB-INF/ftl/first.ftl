<html>
<head><title>Acmee Products International</title>
<body>
  <h1>Hello ${user}!</h1>
  <p>These are our latest offers:
  <ul>
    <#list latestProducts as prod>
      <li>${prod.name} for ${prod.price} Credits.
    </#list>
  </ul>
</body>
</html>