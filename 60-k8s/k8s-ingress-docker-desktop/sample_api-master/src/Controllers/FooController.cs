using System;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using routing.Models;

namespace routing.Controllers
{
    [ApiController]
    [Route("foo")]
    public class FooController : ControllerBase {
        private readonly ILogger<FooController> _logger;

        public FooController(ILogger<FooController> logger)
        {
            _logger = logger;
        }

        [Route("{reference:guid}")]
        public async Task<Foo> GetFoo(Guid reference) {
            var foo = new Foo(reference.ToString());
            return foo;
        }
    }
}