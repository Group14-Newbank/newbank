<h3>LOGIN</h3>

<table>
  <tr>
    <td><strong>Description</strong></td>
    <td colspan="2">Log into the application</td>
  </tr>
  <tr>
    <td><strong>Syntax</strong></td>
    <td colspan="2"><code>LOGIN username password</code></td>
  </tr>
  <tr>
    <td></td>
    <td><em>username</em></td>
    <td><em>The customer’s username</em></td>
  </tr>
  <tr>
    <td></td>
    <td>password</td>
    <td>The customer’s password</td>
  </tr>
  <tr>
    <td><strong>Comments</strong></td>
    <td colspan="2"></td>
  </tr>
  <tr>
    <td><strong>Returns</strong></td>
    <td>SUCCESS</td>
    <td>If the transaction completed successfully</td>
  </tr>
  <tr>
    <td></td>
    <td>FAIL <em>message</em></td>
    <td>If the transaction failed, <em>message</em> provides the error message</td>
  </tr>
  <tr>
    <td><strong>Example</strong></td>
    <td colspan="2"><code>$ LOGIN John john</code><br /><code>SUCCESS</code></td>
  </tr>
</table>

<h3>NEWACCOUNT</h3>

<table>
  <tr>
    <td><strong>Description</strong></td>
    <td colspan="2">Creates a new account for a customer</td>
  </tr>
  <tr>
    <td><strong>Syntax</strong></td>
    <td colspan="2"><code>NEWACCOUNT name</code></td>
  </tr>
  <tr>
    <td></td>
    <td><em>name</em></td>
    <td><em>A label that identifies the account.</em></td>
  </tr>
  <tr>
    <td><strong>Comments</strong></td>
    <td colspan="2">
      <ul>
         <li>Customer must be logged in in order to use this command</li>
         <li>Accounts labels for a customer must be unique</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td><strong>Returns</strong></td>
    <td>SUCCESS</td>
    <td>If the transaction completed successfully</td>
  </tr>
  <tr>
    <td></td>
    <td>FAIL <em>message</em></td>
    <td>If the transaction failed, <em>message</em> provides the error message</td>
  </tr>
  <tr>
    <td><strong>Example</strong></td>
    <td colspan="2"><code>$ NEWACCOUNT Savings</code><br /><code>SUCCESS</code></td>
  </tr>
</table>

<h3>REGISTER</h3>

<table>
  <tr>
    <td><strong>Description</strong></td>
    <td colspan="2">Adds a new customer to the application</td>
  </tr>
  <tr>
    <td><strong>Syntax</strong></td>
    <td colspan="2"><code>REGISTER username password</code></td>
  </tr>
  <tr>
    <td></td>
    <td><em>username</em></td>
    <td><em>The customer’s username</em></td>
  </tr>
  <tr>
    <td></td>
    <td>password</td>
    <td>The customer’s password</td>
  </tr>
  <tr>
    <td><strong>Comments</strong></td>
    <td colspan="2"></td>
  </tr>
  <tr>
    <td><strong>Returns</strong></td>
    <td>SUCCESS</td>
    <td>If the transaction completed successfully</td>
  </tr>
  <tr>
    <td></td>
    <td>FAIL <em>message</em></td>
    <td>If the transaction failed, <em>message</em> provides the error message</td>
  </tr>
  <tr>
    <td><strong>Example</strong></td>
    <td colspan="2"><code>$ REGISTER John john</code><br /><code>SUCCESS</code></td>
  </tr>
</table>

<h3>SHOWMYACCOUNTS</h3>

<table>
  <tr>
    <td><strong>Description</strong></td>
    <td colspan="2">Retrieves and displays a list of all the customers’ accounts along with their current balance</td>
  </tr>
  <tr>
    <td><strong>Syntax</strong></td>
    <td colspan="2"><code>SHOWMYACCOUNTS</code></td>
  </tr>
  <tr>
    <td><strong>Comments</strong></td>
    <td colspan="2">
      <ul>
         <li>Customer must be logged in in order to use this command</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td><strong>Returns</strong></td>
    <td>SUCCESS <em>info</em</td>
    <td>If the transaction completed successfully, <em>info</em> contains the accounts' information</td>
  </tr>
  <tr>
    <td></td>
    <td>FAIL <em>message</em></td>
    <td>If the transaction failed, <em>message</em> provides the error message</td>
  </tr>
  <tr>
    <td><strong>Example</strong></td>
    <td colspan="2"><code>$ SHOWMYACCOUNTS</code><br /><code>SUCCESS Savings 1000.0</code></td>
  </tr>
</table>