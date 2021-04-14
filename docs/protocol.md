<h2>General</h2>

<h3>LOGIN</h3>

<table>
  <tr>
    <td><strong>Description</strong></td>
    <td colspan="2">Log into the application.</td>
  </tr>
  <tr>
    <td><strong>Syntax</strong></td>
    <td colspan="2"><code>LOGIN username password</code></td>
  </tr>
  <tr>
    <td></td>
    <td><em>username</em></td>
    <td>The customer’s username</td>
  </tr>
  <tr>
    <td></td>
    <td><em>password</em></td>
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

<h3>QUIT</h3>

<table>
  <tr>
    <td><strong>Description</strong></td>
    <td colspan="2">Exit the application.</td>
  </tr>
  <tr>
    <td><strong>Syntax</strong></td>
    <td colspan="2"><code>QUIT</code></td>
  </tr>
  <tr>
    <td><strong>Comments</strong></td>
    <td colspan="2">
    </td>
  </tr>
  <tr>
    <td><strong>Returns</strong></td>
    <td>SUCCESS <em>info</em></td>
    <td>If the transaction completed successfully, <em>info</em> contains the accounts' information</td>
  </tr>
  <tr>
    <td><strong>Example</strong></td>
    <td colspan="2"><code>$ QUIT</code><br /><code>SUCCESS Good bye.</code></td>
  </tr>
</table>

<h3>HELP</h3>

<table>
  <tr>
    <td><strong>Description</strong></td>
    <td colspan="2">Get the list of all available commands.</td>
  </tr>
  <tr>
    <td><strong>Syntax</strong></td>
    <td colspan="2"><code>HELP</code></td>
  </tr>
  <tr>
    <td><strong>Comments</strong></td>
    <td colspan="2">
    </td>
  </tr>
  <tr>
    <td><strong>Returns</strong></td>
    <td>SUCCESS <em>info</em></td>
    <td><em>info</em> contains the available commands</td>
  </tr>
  <tr>
    <td><strong>Example</strong></td>
    <td colspan="2"><code>$ HELP</code> SUCCESS ...</td>
  </tr>
</table>

<h2>Account administration</h2>

<h3>DEFAULT</h3>

<table>
  <tr>
    <td><strong>Description</strong></td>
    <td colspan="2">Specify the default current account.</td>
  </tr>
  <tr>
    <td><strong>Syntax</strong></td>
    <td colspan="2"><code>DEFAULT account_name</code></td>
  </tr>
  <tr>
    <td></td>
    <td><em>account_name</em></td>
    <td>A label that identifies the account.</td>
  </tr>
  <tr>
    <td><strong>Comments</strong></td>
    <td colspan="2">
      <ul>
         <li>Customers must be logged in in order to use this command.</li>
         <li>Saving accounts cannot also be the default current account.</li>
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
    <td colspan="2"><code>$ DEFAULT Main</code><br /><code>SUCCESS</code></td>
  </tr>
</table>

<h3>NEWACCOUNT</h3>

<table>
  <tr>
    <td><strong>Description</strong></td>
    <td colspan="2">Creates a new account for a customer.</td>
  </tr>
  <tr>
    <td><strong>Syntax</strong></td>
    <td colspan="2"><code>NEWACCOUNT name [DEFAULT]</code></td>
  </tr>
  <tr>
    <td></td>
    <td><em>name</em></td>
    <td>A label that identifies the account.</td>
  </tr>
  <tr>
    <td></td>
    <td>DEFAULT</td>
    <td>If present this keyword makes the account the default current account.</td>
  </tr>
  <tr>
    <td><strong>Comments</strong></td>
    <td colspan="2">
      <ul>
         <li>Customers must be logged in in order to use this command.</li>
         <li>Accounts labels for a customer must be unique.</li>
         <li>Saving accounts cannot also be the default current account.</li>
         <li>The first non savings account created for a customer will automatically become the default current account.</li>
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
    <td><strong>Example 1</strong></td>
    <td colspan="2"><code>$ NEWACCOUNT Savings</code><br /><code>SUCCESS</code></td>
  </tr>
  <tr>
    <td><strong>Example 2</strong></td>
    <td colspan="2"><code>$ NEWACCOUNT Main DEFAULT</code><br /><code>SUCCESS</code></td>
  </tr>
</table>

<h3>REGISTER</h3>

<table>
  <tr>
    <td><strong>Description</strong></td>
    <td colspan="2">Adds a new customer to the application.</td>
  </tr>
  <tr>
    <td><strong>Syntax</strong></td>
    <td colspan="2"><code>REGISTER username password</code></td>
  </tr>
  <tr>
    <td></td>
    <td><em>username</em></td>
    <td>The customer’s username</td>
  </tr>
  <tr>
    <td></td>
    <td><em>password</em></td>
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

<h2>Transactions</h2>

<h3>SHOWMYACCOUNTS</h3>

<table>
  <tr>
    <td><strong>Description</strong></td>
    <td colspan="2">Retrieves and displays a list of all the customers’ accounts along with their current balance.</td>
  </tr>
  <tr>
    <td><strong>Syntax</strong></td>
    <td colspan="2"><code>SHOWMYACCOUNTS</code></td>
  </tr>
  <tr>
    <td><strong>Comments</strong></td>
    <td colspan="2">
      <ul>
         <li>Customers must be logged in in order to use this command.</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td><strong>Returns</strong></td>
    <td>SUCCESS <em>info</em></td>
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

<h3>DEPOSIT</h3>

<table>
  <tr>
    <td><strong>Description</strong></td>
    <td colspan="2">Add money into one of the customer's own accounts.</td>
  </tr>
  <tr>
    <td><strong>Syntax</strong></td>
    <td colspan="2"><code>DEPOSIT account_name amount</code></td>
  </tr>
  <tr>
    <td></td>
    <td><em>account_name</em></td>
    <td>The account name</td>
  </tr>
  <tr>
    <td></td>
    <td><em>amount</em></td>
    <td>The amount to deposit</td>
  </tr>
  <tr>
    <td><strong>Comments</strong></td>
    <td colspan="2">
      <ul>
         <li>Customers must be logged in in order to use this command.</li>
         <li>The default currency used is GBP.</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td><strong>Returns</strong></td>
    <td>SUCCESS</td>
    <td>If the transaction completed successfully.</td>
  </tr>
  <tr>
    <td></td>
    <td>FAIL <em>message</em></td>
    <td>If the transaction failed, <em>message</em> provides the error message</td>
  </tr>
  <tr>
    <td><strong>Example</strong></td>
    <td colspan="2"><code>$ DEPOSIT Savings 200.0</code><br /><code>SUCCESS</code></td>
  </tr>
</table>

<h3>PAY</h3>

<table>
  <tr>
    <td><strong>Description</strong></td>
    <td colspan="2">Credit a specified customer's default current account.</td>
  </tr>
  <tr>
    <td><strong>Syntax</strong></td>
    <td colspan="2"><code>PAY person amount</code></td>
  </tr>
  <tr>
    <td></td>
    <td><em>person</em></td>
    <td>The recipient's name</td>
  </tr>
  <tr>
    <td></td>
    <td><em>amount</em></td>
    <td>The amount to credit</td>
  </tr>
  <tr>
    <td><strong>Comments</strong></td>
    <td colspan="2">
      <ul>
         <li>Customers must be logged in in order to use this command.</li>
         <li>The default currency used is GBP.</li>
         <li>The amount specified cannot exceed the sender's default account balance.</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td><strong>Returns</strong></td>
    <td>SUCCESS</td>
    <td>If the transaction completed successfully.</td>
  </tr>
  <tr>
    <td></td>
    <td>FAIL <em>message</em></td>
    <td>If the transaction failed, <em>message</em> provides the error message</td>
  </tr>
  <tr>
    <td><strong>Example</strong></td>
    <td colspan="2"><code>$ PAY John 200.0</code><br /><code>SUCCESS</code></td>
  </tr>
</table>

<h3>MOVE</h3>

<table>
  <tr>
    <td><strong>Description</strong></td>
    <td colspan="2">Move money from one account to another.</td>
  </tr>
  <tr>
    <td><strong>Syntax</strong></td>
    <td colspan="2"><code>MOVE account_from account_to amount</code></td>
  </tr>
  <tr>
    <td></td>
    <td><em>account_from</em></td>
    <td>The name of the account from which the money is transferred.</td>
  </tr>
  <tr>
    <td></td>
    <td><em>account_to</em></td>
    <td>The name of the account to which the money is transferred.</td>
  </tr>
  <tr>
    <td><strong>Comments</strong></td>
    <td colspan="2">
      <ul>
         <li>Customers must be logged in in order to use this command.</li>
         <li>The default currency used is GBP.</li>
         <li>The amount specified cannot exceed the balance of the account it is transferred from.</li>
         <li>Two different account names must be provided and both need to belong to the same customer.</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td><strong>Returns</strong></td>
    <td>SUCCESS</td>
    <td>If the transaction completed successfully.</td>
  </tr>
  <tr>
    <td></td>
    <td>FAIL <em>message</em></td>
    <td>If the transaction failed, <em>message</em> provides the error message</td>
  </tr>
  <tr>
    <td><strong>Example</strong></td>
    <td colspan="2"><code>$ MOVE Main Savings 150.15</code><br /><code>SUCCESS</code></td>
  </tr>
</table>

<h2>Micro-loans</h2>

<h3>REQUESTLOAN</h3>

<table>
  <tr>
    <td><strong>Description</strong></td>
    <td colspan="2">Request a peer-to-peer loan. Requests can be seen by all other users.</td>
  </tr>
  <tr>
    <td><strong>Syntax</strong></td>
    <td colspan="2"><code>REQUESTLOAN amount repayment_term_days</code></td>
  </tr>
  <tr>
    <td><strong>Comments</strong></td>
    <td colspan="2">
      <ul>
         <li>Customers must be logged in in order to use this command.</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td><strong>Returns</strong></td>
    <td>SUCCESS</td>
    <td>If the request is made successfully</td>
  </tr>
  <tr>
    <td></td>
    <td>FAIL <em>message</em></td>
    <td>If the request failed, <em>message</em> provides the error message</td>
  </tr>
  <tr>
    <td><strong>Example</strong></td>
    <td colspan="2"><code>$ REQUESTLOAN 1000 365</code><br /><code>SUCCESS</code></td>
  </tr>
</table>