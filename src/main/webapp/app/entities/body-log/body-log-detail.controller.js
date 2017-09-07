(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('BodyLogDetailController', BodyLogDetailController);

    BodyLogDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'BodyLog', 'User'];

    function BodyLogDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, BodyLog, User) {
        var vm = this;

        vm.bodyLog = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('foodlogbotadmApp:bodyLogUpdate', function(event, result) {
            vm.bodyLog = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
