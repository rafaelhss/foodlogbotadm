(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('JacaController', JacaController);

    JacaController.$inject = ['Jaca'];

    function JacaController(Jaca) {

        var vm = this;

        vm.jacas = [];

        loadAll();

        function loadAll() {
            Jaca.query(function(result) {
                vm.jacas = result;
                vm.searchQuery = null;
            });
        }
    }
})();
