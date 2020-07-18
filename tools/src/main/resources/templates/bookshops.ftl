<section class="post">
  <div class="card bg-light mb-2 border-dark " style="max-width:100%">
    <div class="card-header"><i class="fas fa-book-reader"></i> Independent Book Shops - Mail Order. See Map below</div>
    <div class="card-body p-0">
      <div class="scroll-table">
        <table id="book-stores" style="width:100%" class="pure-table">
          <thead>
          <tr>
            <th style="width:30%">Shop</th>
            <th style="width:50%">Address</th>
            <th style="width:10%">Website</th>
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
            </tr>
          </#list>
          <script>
            $(document).ready(function () {
              $('#book-stores').DataTable({
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
