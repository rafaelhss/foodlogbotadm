(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('JacaDeleteController',JacaDeleteController);

    JacaDeleteController.$inject = ['$uibModalInstance', 'entity', 'Jaca'];

    function JacaDeleteController($uibModalInstance, entity, Jaca) {
        var vm = this;

        vm.jaca = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Jaca.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
