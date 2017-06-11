(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('ClientController', ClientController);

    ClientController.$inject = ['Client'];

    function ClientController(Client) {

        var vm = this;

        vm.clients = [];

        loadAll();

        function loadAll() {
            Client.query(function(result) {
                vm.clients = result;
                vm.searchQuery = null;
            });
        }
    }
})();
