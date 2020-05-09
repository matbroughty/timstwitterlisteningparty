<section class="post">
  <div class="card bg-light mb-2 border-dark " style="max-width">
    <div class="card-header"><i class="fas fa-record-vinyl"></i> Independent Record Shops - Mail Order. See Map below
    </div>
    <div class="card-body p-0">
      <div class="scroll-table">
        <table id="record-stores" width="100%" class="pure-table">
          <thead>
          <tr>
            <th width="30%">Shop</th>
            <th width="50%">Address</th>
            <th width="10%">Website</th>
            <th width="10%">Twitter</th>
          </tr>
          </thead>
          <tbody>
          <#list shops as shop>
          <tr>
            <td>${shop.name}</td>
            <td>${shop.address}</td>
            <td><a
              <#if shop.webSite?has_content >
              class="pure-button"
              <#else>
              class="pure-button-disabled"
               </#if>
              href="${shop.webSite}"><i class="fas fa-laptop"></i></a></td>
            <td><a
              <#if shop.twitterLink?has_content >
              class="pure-button"
              <#else>
              class="pure-button-disabled"
            </#if>

                   href="${shop.twitterLink}"><i
              class="fab fa-twitter-square"></i></a></td>
          </tr>
          </#list>
          <script>
    $(document).ready(function() {
      $('#record-stores').DataTable({
        "paging": false
      });
    });

          </script>
          </tbody>
        </table>
      </div>
    </div>
  </div>

</section>
