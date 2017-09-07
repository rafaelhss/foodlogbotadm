(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('BodyLogDeleteController',BodyLogDeleteController);

    BodyLogDeleteController.$inject = ['$uibModalInstance', 'entity', 'BodyLog'];

    function BodyLogDeleteController($uibModalInstance, entity, BodyLog) {
        var vm = this;

        vm.bodyLog = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            BodyLog.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
