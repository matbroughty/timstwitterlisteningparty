<div class="container-fluid">
  <#list reviews as review>
    <div class="row d-flex mt-3">
      <div class="card" style="width: 100%;">
        <div class="card-header font-weight-bold">
          ${review.author} : ${review.title}
        </div>
        <div class="card-body">
          <p class="card-text">${review.description}</p>
          <a class="btn btn-primary"
             href="${review.bookStoreWebsite}"><i
                    class="fas fa-laptop"></i> Buy Here</a>
        </div>
        <div class="card-footer text-muted">
          <a class="btn btn-light" href="${review.reviewerTwitter}"><i
                    class="fab fa-twitter"> ${review.reviewerInitials}</i></a>
        </div>
      </div>
    </div>
  </#list>
</div>
